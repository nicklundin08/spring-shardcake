package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class App


object App {
  def main(args: Array[String]): Unit = SpringApplication.run(classOf[App])
}
