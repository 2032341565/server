/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.im.imhandler;

import cn.wildfirechat.im.persistence.MemorySessionStore;
import cn.wildfirechat.proto.WFCMessage;
import cn.wildfirechat.im.spi.impl.Qos1PublishHandler;
import cn.wildfirechat.im.spi.impl.security.TokenAuthenticator;
import io.netty.buffer.ByteBuf;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.util.IMTopic;

@Handler(IMTopic.GetTokenTopic)
public class GetTokenHandler extends IMHandler<WFCMessage.GetTokenRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.GetTokenRequest request, Qos1PublishHandler.IMCallback callback) {
        MemorySessionStore.Session session = m_sessionsStore.updateOrCreateUserSession(fromUser, clientID, request.getPlatform());
        TokenAuthenticator authenticator = new TokenAuthenticator();
        String strToken = authenticator.generateToken(fromUser);
        String result = strToken + "|" + session.getSecret() + "|" + session.getDbSecret();
        byte[] data = result.getBytes();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
