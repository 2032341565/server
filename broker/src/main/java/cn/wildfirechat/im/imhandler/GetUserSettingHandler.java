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

import static cn.wildfirechat.common.ErrorCode.ERROR_CODE_SUCCESS;

@Handler(IMTopic.GetUserSettingTopic)
public class GetUserSettingHandler extends IMHandler<WFCMessage.Version> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.Version request, Qos1PublishHandler.IMCallback callback) {
            WFCMessage.GetUserSettingResult.Builder builder = WFCMessage.GetUserSettingResult.newBuilder();
            ErrorCode errorCode = m_messagesStore.getUserSettings(fromUser, request.getVersion(), builder);
            if (errorCode == ERROR_CODE_SUCCESS) {
                byte[] data = builder.build().toByteArray();
                ackPayload.ensureWritable(data.length).writeBytes(data);
            }
            return errorCode;
    }
}
