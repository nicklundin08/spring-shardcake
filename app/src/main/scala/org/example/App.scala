package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import zio._
import scala.util._
import com.devsisters.shardcake._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@SpringBootApplication
class App


object App {
  val logger = LoggerFactory.getLogger("App")
  val runtime = Runtime.default

  def main(args: Array[String]): Unit = SpringApplication.run(classOf[App])

  @org.springframework.context.event.EventListener
  def initShardCake(e: org.springframework.context.event.ContextRefreshedEvent) = {
    App.initShardCakeUnsafe
  }

  val initShardCakeEff = for {
    _     <- ZIO.attempt(logger.info("Trace message from inside effect"))
    _     <- Sharding.registerEntity(Guild, Guild.behavior)
    _     <- Sharding.registerScoped
  } yield (7)


  val initShardCakeTask: Task[Int] =
    ZIO.scoped(initShardCakeEff.debug).provide(
    ZLayer.succeed(com.devsisters.shardcake.Config.default),
    ZLayer.succeed(GrpcConfig.default),
    com.devsisters.shardcake.interfaces.Serialization.javaSerialization, // use java serialization for messages
    com.devsisters.shardcake.interfaces.Storage.memory,                  // store data in memory
    ShardManagerClient.liveWithSttp, // client to communicate with the Shard Manager
    GrpcPods.live,                   // use gRPC protocol
    GrpcShardingService.live,        // expose gRPC service
    Sharding.live                    // sharding logic
  )
    
  def initShardCakeUnsafe: Unit = {
    logger.info("Trace message 2");
    val result = Unsafe.unsafe { implicit unsafe =>
      logger.info("Trace message 4");
      val innerResult = runtime.unsafe.run(initShardCakeTask).getOrThrowFiberFailure()
      logger.info("Trace message 5");
      innerResult
    }
    logger.info("Trace message 6. Result it " + result);
  }
}
