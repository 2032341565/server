/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.im.imhandler;

import cn.wildfirechat.proto.WFCMessage;
import cn.wildfirechat.pojos.GroupNotificationBinaryContent;
import cn.wildfirechat.im.spi.impl.Qos1PublishHandler;
import io.netty.buffer.ByteBuf;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.util.IMTopic;

import static cn.wildfirechat.common.ErrorCode.ERROR_CODE_SUCCESS;

@Handler(value = IMTopic.AddGroupMemberTopic)
public class AddGroupMember extends GroupHandler<WFCMessage.AddGroupMemberRequest> {

    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.AddGroupMemberRequest request, Qos1PublishHandler.IMCallback callback) {
        ErrorCode errorCode = m_messagesStore.addGroupMembers(fromUser, isAdmin, request.getGroupId(), request.getAddedMemberList());
        if (errorCode == ERROR_CODE_SUCCESS) {
            if (request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), request.getNotifyContent());
            } else {
                WFCMessage.MessageContent content = new GroupNotificationBinaryContent(request.getGroupId(), fromUser, null, getMemberIdList(request.getAddedMemberList())).getAddGroupNotifyContent();
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), content);
            }
        }

        return errorCode;
    }
}
