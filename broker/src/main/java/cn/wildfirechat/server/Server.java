/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.server;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        //im服务器启动入口
        cn.wildfirechat.im.server.Server.start(args);
    }
}
