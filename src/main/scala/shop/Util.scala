package shop

import scala.io.Source

object Util {
  def readFile(fileName : String) : String = {
    val contents:StringBuilder = new StringBuilder()
		for (line <- Source.fromFile(fileName).getLines) {
			contents.append(line)
		}
		contents.toString
	}	
  def readFromStream(filename:String) : String = {
    val contents:StringBuilder = new StringBuilder()
    val stream:java.io.InputStream = this.getClass().getClassLoader().getResourceAsStream(filename)
		for (line <- Source.fromInputStream(stream).getLines) {
			contents.append(line)
		}
		contents.toString
  }
}