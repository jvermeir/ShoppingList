package nl.vermeir.shopapi

import org.springframework.stereotype.Service
import java.util.*

@Service
class UUIDGenerator {
  fun generate(): UUID = UUID.randomUUID()
}
