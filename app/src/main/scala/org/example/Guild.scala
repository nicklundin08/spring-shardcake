package org.example

import zio._
import scala.util._
import com.devsisters.shardcake._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

sealed trait GuildMessage

object GuildMessage {
  case class Join(userId: String, replier: Replier[Try[Set[String]]]) extends GuildMessage
  case class Leave(userId: String)                                    extends GuildMessage
}


object Guild extends EntityType[GuildMessage]("guild"){
    val logger = LoggerFactory.getLogger("Guild")
    val runtime = Runtime.default


    def behavior(entityId: String, messages: Queue[GuildMessage]): RIO[Sharding, Nothing] =
        Ref
            .make(Set.empty[String])
            .flatMap(state => messages.take.flatMap(handleMessage(state, _)).forever)


    def handleMessage(state: Ref[Set[String]], message: GuildMessage): RIO[Sharding, Unit] =
        message match {
            case GuildMessage.Join(userId, replier) =>
            state.get.flatMap(members =>
                if (members.size >= 5)
                replier.reply(Failure(new Exception("Guild is already full!")))
                else
                state.updateAndGet(_ + userId).flatMap { newMembers =>
                    replier.reply(Success(newMembers))
                }
            )
            case GuildMessage.Leave(userId)         =>
            state.update(_ - userId)
        }


    val programEffect =
        for {
        _    <- ZIO.attempt(logger.info("Trace message from inside effect"))
        _     <- Sharding.registerEntity(Guild, Guild.behavior)
        _     <- Sharding.registerScoped
        guild <- Sharding.messenger(Guild)
        _     <- guild.send("guild1")(GuildMessage.Join("user1", _)).debug
        _     <- guild.send("guild1")(GuildMessage.Join("user2", _)).debug
        _     <- guild.send("guild1")(GuildMessage.Join("user3", _)).debug
        _     <- guild.send("guild1")(GuildMessage.Join("user4", _)).debug
        _     <- guild.send("guild1")(GuildMessage.Join("user5", _)).debug
        _     <- guild.send("guild1")(GuildMessage.Join("user6", _)).debug
        } yield (7)


    def programEffectWithDependencies: Task[Int] =
        ZIO.scoped(programEffect).provide(
        ZLayer.succeed(com.devsisters.shardcake.Config.default),
        ZLayer.succeed(GrpcConfig.default),
        com.devsisters.shardcake.interfaces.Serialization.javaSerialization, // use java serialization for messages
        com.devsisters.shardcake.interfaces.Storage.memory,                  // store data in memory
        ShardManagerClient.liveWithSttp, // client to communicate with the Shard Manager
        GrpcPods.live,                   // use gRPC protocol
        GrpcShardingService.live,        // expose gRPC service
        Sharding.live                    // sharding logic
        )
    
    def runProgramUnsafe: Unit = {
        logger.info("Trace message 2");
        val result = Unsafe.unsafe { implicit unsafe =>
            logger.info("Trace message 4");
            val innerResult = runtime.unsafe.run(programEffectWithDependencies).getOrThrowFiberFailure()
            logger.info("Trace message 5");
            innerResult
        }
        logger.info("Trace message 6. Result it " + result);
    }
}
