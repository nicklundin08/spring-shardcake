package org.example

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.{RequestMapping, RestController}
import zio._
import scala.util._
import com.devsisters.shardcake._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime

@RestController
class ZioController:

  @RequestMapping(path = Array("/rest/test"), method = Array(GET))
  def root(): Map[String, Any] = {
    ZioController.responseMap
  }

  @RequestMapping(path = Array("/rest/basic-effect"), method = Array(GET))
  def basicEffect(): Map[String, Any] = {
    ZioController.logger.info("Trace message 1");
    TestEff.runProgramUnsafe
    ZioController.responseMap
  }

  @RequestMapping(path = Array("/rest/guild"), method = Array(GET))
  def guild(): Map[String, Any] = {
    ZioController.logger.info("Trace message 1");
    Guild.runProgramUnsafe
    ZioController.responseMap
  }


object ZioController {
  val logger = LoggerFactory.getLogger("ZioController");
  val responseMap:  Map[String, Any] = Map("name" -> "Some app name", "message" -> "It works on my machine!")
}