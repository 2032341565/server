/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.im.imhandler;

import cn.wildfirechat.proto.WFCMessage;
import cn.wildfirechat.im.spi.impl.Qos1PublishHandler;
import io.netty.buffer.ByteBuf;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.util.IMTopic;

import java.util.List;

@Handler(IMTopic.UserSearchTopic)
public class UserSearchHandler extends IMHandler<WFCMessage.SearchUserRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.SearchUserRequest request, Qos1PublishHandler.IMCallback callback) {
        List<WFCMessage.User> users = m_messagesStore.searchUser(request.getKeyword(), request.getFuzzy(), request.getPage());
        WFCMessage.SearchUserResult.Builder builder = WFCMessage.SearchUserResult.newBuilder();
        builder.addAllEntry(users);
        byte[] data = builder.build().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
