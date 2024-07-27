package org.example

import zio._
import scala.util._
import com.devsisters.shardcake._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object TestEff {
    val logger = LoggerFactory.getLogger("TestEff")
    val runtime = Runtime.default


    val programEffect =
        for {
        _     <- ZIO.attempt(logger.info("Trace message from inside effect"))
        } yield (7)

    def runProgramUnsafe: Unit = {
        logger.info("Trace message 2");
        val result = Unsafe.unsafe { implicit unsafe =>
            logger.info("Trace message 4");
            val innerResult = runtime.unsafe.run(programEffect).getOrThrowFiberFailure()
            logger.info("Trace message 5");
            innerResult
        }
        logger.info("Trace message 6. Result it " + result);
    }
}
