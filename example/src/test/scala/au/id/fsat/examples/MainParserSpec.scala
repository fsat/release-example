/*
 * Copyright 2018 Felix Satyaputra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
