package net.ayataka.kordis.websocket.handlers.channel

import com.google.gson.JsonObject
import net.ayataka.kordis.DiscordClientImpl
import net.ayataka.kordis.entity.server.ServerImpl
import net.ayataka.kordis.event.events.server.channel.ChannelDeleteEvent
import net.ayataka.kordis.websocket.handlers.GatewayHandler

class ChannelDeleteHandler : GatewayHandler {
    override val eventType = "CHANNEL_DELETE"

    override fun handle(client: DiscordClientImpl, data: JsonObject) {
        val server = client.servers.find(data["guild_id"].asLong) as? ServerImpl
        val id = data["id"].asLong

        if (server == null) {
            client.privateChannels.remove(id)
            return
        }

        val channel = server.textChannels.remove(id)
                ?: server.voiceChannels.remove(id)
                ?: server.channelCategories.remove(id)

        channel?.let { client.eventManager.fire(ChannelDeleteEvent(it)) }
    }
}