package work.chenxuan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 辰轩
 * @version 1.0
 * @date 2021/8/3 21:29 星期二
 */


public class Server {
    /**
     * 匹配路由信息
     */
    private static final Pattern ROUTER_PATTERN = Pattern.compile("/[-\\w\u4e00-\u9fa5]*\\?" +
            "[-\\w\u4e00-\u9fa5]*=[-\\w\u4e00-\u9fa5]*&[-\\w\u4e00-\u9fa5]*=[-\\w\u4e00-\u9fa5\\s]*(?= HTTP)");


    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(80);
            System.out.println("启动服务器....");
            while (true) {
                Socket s = ss.accept();
                System.out.println("客户端:" + s.getInetAddress() + "已连接到服务器");

                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String str = br.readLine();
                String router = null;
                String params = null;
                Matcher routerMatcher = ROUTER_PATTERN.matcher(str);
                Matcher paramMatcher;
                String message = null;
                Map<String, String> paramsMap = new LinkedHashMap<>();
                // 乘法结果
                BigInteger result;

                // 获取 /xx?xx=xx
                if (routerMatcher.find()) {
                    router = routerMatcher.group(0);
                    System.out.println("router---" + router);

                    // 分割路由和参数
                    if (router != null) {
                        String[] split = router.split("\\?");
                        router = split[0];
                        params = split[1];
                    } else {
                        System.out.println("无参数");
                    }

                    // 将参数取出
                    if (params != null) {
                        String[] split = params.split("&");
                        // 将所有参数存储在map中
                        for (String tem : split) {
                            String[] split1 = tem.split("=");
                            // 看是否已经存储重复的param key
                            if (paramsMap.containsKey(split1[0])) {
                                String value = paramsMap.get(split1[0]);
                                paramsMap.replace(split1[0], value, value + "," + split1[1]);
                            } else {
                                paramsMap.put(split1[0], split1[1]);
                            }
                        }
                    }
                    String a = paramsMap.get("a");
                    String b = paramsMap.get("b");

                    // 判断路由是不是/mult
                    if ("/mult".equals(router)) {
                        if (a == null || b == null) {
                            System.out.println("缺少参数");
                            message = "缺少参数";
                        } else {
                            try {
                                BigInteger bigA = new BigInteger(a);
                                BigInteger bigB = new BigInteger(b);
                                result = bigA.multiply(bigB);
                                message = "结果为" + result.toString();
                            } catch (NumberFormatException e) {
                                System.out.println("参数类型错误");
                                message = "参数类型错误";
                            }
                        }
                    } else if ("/add".equals(router)) {
                        if (a == null || b == null) {
                            System.out.println("缺少参数");
                            message = "缺少参数";
                        } else {
                            try {
                                BigInteger bigA = new BigInteger(a);
                                BigInteger bigB = new BigInteger(b);
                                result = bigA.add(bigB);
                                message = "结果为" + result.toString();
                            } catch (NumberFormatException e) {
                                System.out.println("参数类型错误");
                                message = "参数类型错误";
                            }
                        }
                    } else {
                        System.out.println("路由不匹配");
                        message = "404 not found";
                    }
                } else {
                    System.out.println("请检查url");
                    message = "404 not found";
                }


                // 输出结果到页面
                PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
                printWriter.println("HTTP/1.1 200 OK");
                printWriter.println("Content-Type:text/html;charset=utf-8");
                printWriter.println();

                printWriter.println("<h1>" + message + "</h1>");

                printWriter.close();
                br.close();
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
