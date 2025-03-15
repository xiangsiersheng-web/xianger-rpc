package com.ws.rpc.example.client;

import com.ws.rpc.example.client.controller.HelloController;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author ws
 * @version 1.0
 * @date 2025-03-15 21:07
 */
@BenchmarkMode({Mode.All})
@Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
//测量次数,每次测量的持续时间
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(32)
@Fork(1)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Slf4j
public class JMHTest {
    private ConfigurableApplicationContext context;
    private HelloController helloController;

    @Setup(Level.Trial)
    public void init() {
        if (context == null) {
//            context = SpringApplication.run(ClientApplication.class, new String[]{});
            context = new SpringApplicationBuilder(ClientApplication.class)
                    .web(WebApplicationType.NONE)  // 明确禁用 Web 环境
                    .run();
            helloController = context.getBean(HelloController.class);
        }
    }

    @Benchmark
    public void testSayHello() {
        helloController.hello("ws");
    }

    @TearDown(Level.Trial)
    public void close() {
        if (context != null) {
            context.close();
        }
    }

    public static void main(String[] args) throws RunnerException {
        log.info("测试开始");
        Options opt = new OptionsBuilder()
                .include(JMHTest.class.getSimpleName())
                // 报告输出
                .result("result.json")
                // 报告格式
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }
}
