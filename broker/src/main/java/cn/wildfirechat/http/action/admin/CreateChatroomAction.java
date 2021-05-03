/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.http.action.admin;

import cn.wildfirechat.http.RestResult;
import cn.wildfirechat.http.annotation.HttpMethod;
import cn.wildfirechat.http.annotation.Route;
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.proto.WFCMessage;
import com.google.gson.Gson;
import cn.wildfirechat.pojos.InputCreateChatroom;
import cn.wildfirechat.pojos.OutputCreateChatroom;
import cn.wildfirechat.im.spi.impl.Utils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import cn.wildfirechat.common.ErrorCode;

@Route(APIPath.Create_Chatroom)
@HttpMethod("POST")
public class CreateChatroomAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest)request.getNettyRequest();
            byte[] bytes = Utils.readBytesAndRewind(fullHttpRequest.content());
            String content = new String(bytes);
            Gson gson = new Gson();
            InputCreateChatroom inputCreateChatroom = gson.fromJson(content, InputCreateChatroom.class);
            if (inputCreateChatroom != null
                && !StringUtil.isNullOrEmpty(inputCreateChatroom.getTitle())) {

                if (StringUtil.isNullOrEmpty(inputCreateChatroom.getChatroomId())) {
                    inputCreateChatroom.setChatroomId(messagesStore.getShortUUID());
                }

                if (inputCreateChatroom.getPortrait() == null || inputCreateChatroom.getPortrait().length() == 0) {
                    inputCreateChatroom.setPortrait("https://avatars.io/gravatar/" + inputCreateChatroom.getChatroomId());
                }

                WFCMessage.ChatroomInfo info = inputCreateChatroom.toChatroomInfo();

                messagesStore.createChatroom(inputCreateChatroom.getChatroomId(), info);

                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.ok(new OutputCreateChatroom(inputCreateChatroom.getChatroomId()));
                response.setContent(new Gson().toJson(result));

            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(new Gson().toJson(result));
            }

        }
        return true;
    }
}
