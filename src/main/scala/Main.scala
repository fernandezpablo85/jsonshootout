package benchmarks

import scala.concurrent.duration._

case class Example(string: String, int: Int, other: Other)
case class Other(string: String, more: List[More])
case class More(int: Double, string: String)

object Main extends App {

  val example = Example("foo", 1, Other("bar \"with\" quotes ", List(More(1 / 3, "one"), More(2.0, "two"))))
  val Alternatives = List(LiftJson, SprayJson, PlayJson, Json4SNative, Json4SJackson)
  val unit = MICROSECONDS
  val iterations = 1000000

  println(s"Bechmarking $iterations iterations, in $unit")

  for (alternative <- Alternatives) {

    println()
    println(s"==== ${alternative.name} ====")
    println(s"==== Sample Results ====")
    println()

    val sample = alternative.serialize(example)
    println("serialized: " + sample)
    println("deserialized: " + alternative.deserialize(sample))
    println()

    val serializeResults = Mark.measure(iterations, unit) {
      alternative.serialize(example)
    }
    printResults("toJson", serializeResults)

    val json = alternative.serialize(example)
    val deserializeResults = Mark.measure(iterations, unit) {
      alternative.deserialize(json)
    }
    printResults("fromJson", deserializeResults)
  }

  private def printResults(title: String, results: Array[Double]) {
    def floor(d: Double) = scala.math.floor(d).toInt
    import scala.util.Sorting.quickSort

    quickSort(results)
    val fifty = results(floor(results.length * 0.5))
    val seventy = results(floor(results.length * 0.70))
    val eighty = results(floor(results.length * 0.80))
    val ninety = results(floor(results.length * 0.90))
    val ninetyFive = results(floor(results.length * 0.95))
    val twoNines = results(floor(results.length * 0.99))
    val mean = results.sum / results.length

    println(s"$title results:")
    println("50%   " + floor(fifty))
    println("70%   " + floor(seventy))
    println("80%   " + floor(eighty))
    println("90%   " + floor(ninety))
    println("95%   " + floor(ninetyFive))
    println("99%   " + floor(twoNines))
    println("mean  " + floor(mean))
  }
}

trait JsonLibrary {
  def name: String
  def serialize(ex: Example): String
  def deserialize(json: String): Example
}

// Lift.
object LiftJson extends JsonLibrary {
  import net.liftweb.json.Serialization
  import net.liftweb.json.JsonParser._
  import net.liftweb.json.NoTypeHints

  val name = "LiftJson"
  implicit val formats = Serialization.formats(NoTypeHints)

  def serialize(ex: Example) = Serialization.write(ex)
  def deserialize(json: String) = parse(json).extract[Example]
}

// Spray.
object SprayJson extends JsonLibrary {
  import spray.json._
  import DefaultJsonProtocol._

  implicit val MoreFormat = jsonFormat2(More)
  implicit val OtherFormat = jsonFormat2(Other)
  implicit val ExampleFormat = jsonFormat3(Example)

  val name = "Spray"
  def serialize(ex: Example) = ex.toJson.compactPrint
  def deserialize(json: String) = json.asJson.convertTo[Example]
}

// Play.
object PlayJson extends JsonLibrary {
  import play.api.libs.json._
  import play.api.libs.functional._

  implicit val MoreFormat = Json.format[More]
  implicit val OtherFormat = Json.format[Other]
  implicit val ExampleFormat = Json.format[Example]

  val name = "Play"
  def serialize(ex: Example) = Json.stringify(Json.toJson(ex))
  def deserialize(json: String) = Json.fromJson[Example](Json.parse(json)).get
}

// Json4SNative.
object Json4SNative extends JsonLibrary {
  import org.json4s._
  import org.json4s.native.JsonMethods._
  import org.json4s.native.Serialization

  implicit val formats = DefaultFormats
  val name = "Json4SNative"
  def serialize(ex: Example) = Serialization.write(ex)
  def deserialize(json: String) = parse(json).extract[Example]
}

// Json4SJackson.
object Json4SJackson extends JsonLibrary {
  import org.json4s._
  import org.json4s.jackson.JsonMethods._
  import org.json4s.jackson.Serialization

  implicit val formats = DefaultFormats

  val name = "Json4SJackson"
  def serialize(ex: Example) = Serialization.write(ex)
  def deserialize(json: String) = parse(json).extract[Example]
}
