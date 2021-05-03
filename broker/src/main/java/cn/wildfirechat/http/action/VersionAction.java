/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.http.action;

import com.google.gson.Gson;
import cn.wildfirechat.http.annotation.HttpMethod;
import cn.wildfirechat.http.annotation.Route;
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.http.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import cn.wildfirechat.util.GitRepositoryState;
import cn.wildfirechat.util.Utility;

import java.io.IOException;

@Route("/api/version")
@HttpMethod("GET")
public class VersionAction extends Action {


    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            response.setStatus(HttpResponseStatus.OK);

            try {

                response.setContent(Utility.formatJson(new Gson().toJson(GitRepositoryState.getGitRepositoryState())));
            } catch (IOException e) {
                e.printStackTrace();
                response.setContent("{\"version\":\"unknown\"}");
            }
        }
        return true;
    }
}
