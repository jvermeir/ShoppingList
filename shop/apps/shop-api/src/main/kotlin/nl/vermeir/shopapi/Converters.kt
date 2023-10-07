package nl.vermeir.shopapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path

data class ConverterResult(val count: Int)

@RestController
class ConverterResource {
  @Autowired
  lateinit var categoryService: CategoryService

  @Value("\${dataFolderPath}")
  private val dataFolderPath: String = "data/production"

  @PostMapping("/converters/{datafileName}")
  fun convert(@PathVariable(name = "datafileName") datafileName: String): ConverterResult {
    val fileName = datafileName.split("/").last()
    val fullFileName = Path.of(dataFolderPath, fileName)
    val file = fullFileName.toFile()
    var count = 0
    file.readLines().forEach {
      val categoryParts = it.split(":")
      val category =
        Category(name = categoryParts[0], shopOrder = categoryParts[1].toInt())
      categoryService.save(category)
      count++
    }

    return ConverterResult(count)
  }
}
