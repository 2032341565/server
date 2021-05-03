/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.http.action.robot;

import cn.wildfirechat.http.action.Action;
import cn.wildfirechat.http.annotation.HttpMethod;
import cn.wildfirechat.http.annotation.Route;
import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.proto.WFCMessage;
import com.google.gson.Gson;
import cn.wildfirechat.http.RestResult;
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.pojos.InputOutputUserInfo;
import cn.wildfirechat.pojos.InputGetUserInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import cn.wildfirechat.common.ErrorCode;

@Route(APIPath.Robot_User_Info)
@HttpMethod("POST")
public class GetUserAction extends RobotAction {

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputGetUserInfo inputUserId = getRequestBody(request.getNettyRequest(), InputGetUserInfo.class);
            if (inputUserId != null
                && (!StringUtil.isNullOrEmpty(inputUserId.getUserId()) || !StringUtil.isNullOrEmpty(inputUserId.getName()) || !StringUtil.isNullOrEmpty(inputUserId.getMobile()))) {

                WFCMessage.User user = null;
                if(!StringUtil.isNullOrEmpty(inputUserId.getUserId())) {
                    user = Action.messagesStore.getUserInfo(inputUserId.getUserId());
                } else if(!StringUtil.isNullOrEmpty(inputUserId.getName())) {
                    user = Action.messagesStore.getUserInfoByName(inputUserId.getName());
                } else if(!StringUtil.isNullOrEmpty(inputUserId.getMobile())) {
                    user = Action.messagesStore.getUserInfoByMobile(inputUserId.getMobile());
                }

                response.setStatus(HttpResponseStatus.OK);
                RestResult result;
                if (user == null) {
                    result = RestResult.resultOf(ErrorCode.ERROR_CODE_NOT_EXIST);
                } else {
                    result = RestResult.ok(InputOutputUserInfo.fromPbUser(user));
                }

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
