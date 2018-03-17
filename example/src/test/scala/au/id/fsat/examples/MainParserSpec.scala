package au.id.fsat.examples

import org.scalatest.{ FunSpec, Matchers }

class MainParserSpec extends FunSpec with Matchers {
  describe("parser") {
    it("parses correct argument") {
      val args = Seq("--host", "0.0.0.0", "--port", "8080")
      Main.parser.parse(args, Main.InputArgs()) shouldBe Some(Main.InputArgs(Some("0.0.0.0"), Some(8080)))
    }
  }
}
