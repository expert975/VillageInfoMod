/**
 * Copyright 2016 VillageInfoMod (Jiraiyah)
 *
 * project link : http://minecraft.curseforge.com/projects/village-info
 *
 * Licensed under The MIT License (MIT);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jiraiyah.villageinfo.network;

import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import jiraiyah.villageinfo.events.WorldDataCollector;
import jiraiyah.villageinfo.inits.NetworkMessages;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class VillagePlayerMessage implements IMessageHandler<VillagePlayerMessage.Packet, IMessage>
{
	@Override
	public IMessage onMessage (VillagePlayerMessage.Packet message, MessageContext ctx )
	{
		if (message.adding)
			WorldDataCollector.addPlayerToVillageList(message.playerId);
		else
			WorldDataCollector.removePlayerFromVillageList(message.playerId);
		return null;
	}

	public static void sendMessage(UUID playerId, boolean adding)
	{
		Packet packet = new Packet(playerId, adding);
		NetworkMessages.network.sendToServer(packet);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public static class Packet implements IMessage
	{
		public UUID playerId;
		public boolean adding;

		public Packet()
		{

		}

		public Packet(UUID playerId, boolean adding)
		{
			this.playerId = playerId;
			this.adding = adding;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			adding = buf.readBoolean();
			String id = new String(ByteBufUtil.getBytes(buf), Charset.forName("UTF-8"));
			playerId = UUID.fromString(id);
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeBoolean(adding);
			String id = playerId.toString();
			byte[] bytes = id.getBytes();
			buf.writeBytes(bytes);
		}
	}
}
