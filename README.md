# Hystrix Example
A simple example of using hystrix for a REST API.  Built to run on openshift.

# How to Deploy
Sorry I don't have very great steps here, but the basic outline is:
1. Create a JBoss EAP app on OpenShift.
2. Add this repo (or your fork) as a remote in your local checkout.
3. Merge master branch of the remote added into your `master` branch.
4. Push to OpenShift.

# Projects Used

## Hystrix
Hystrix is an open source project from Netflix that:
* Hystrix is a latency and fault tolerance library designed to isolate points of access to remote systems, services and 3rd party libraries, stop cascading failure and enable resilience in complex distributed systems where failure is inevitable.

With this project java code with external dependencies can easily be written to include resiliency from degradation and failures with an option to define a fall-back operation in the case that the command is outside of acceptable bounds.  This document will focus on a simple scenario with default configurations, but you can tune things like concurrent execution of the command and specify timeouts.  Another document is likely to be written to document these specific features once it is all figured out.
 
## Servo
servo is an open source application monitoring library from Netflix.  The two major publishers provided are for Graphite and CloudWatch.  There are many pollers available as well, but basically it can publish any metric from JMX.
 
## Hystrix + Servo
Servo  integrates directly with Hystrix to publish all metrics collected from a Hystrix command, documented on Metrics and Monitoring.  Some of the more interesting ones that we'd get good use out of are:
* isCircuitBreakerOpen - is the command processing events or not (open circuit = not processing)
* errorPercentage - percentage of command executions that are failing
* countFailure - number of failed executions
* countSuccess - number of successful executions
* countTimeout - number of executions that have timed out

# Setup: Basic
This section will provide enough code to convey what is happening, but won't be a full fledged application.  This repo is an example application that can be deployed on OpenShift.
 
## Hystrix
This example shows a simple command that takes a string argument that represents the number of milliseconds the command will sleep.  The command converts the input to an integer, executes the thread sleep, then returns the original milliseconds argument.  When the command fails (number format exception or timeout) it returns the original milliseconds argument with "TIMEOUT" concatenated.  This makes it easy to see when the command is successful vs failing.

Required dependencies in pom.xml:
```xml
<dependency>  
    <groupId>com.netflix.hystrix</groupId>  
    <artifactId>hystrix-core</artifactId>  
    <version>1.3.9</version>  
</dependency>  
```

Source:
```java
import com.netflix.hystrix.HytrixCommand;  
import com.netflix.hystrix.HystrixCommandGroupKey;  
  
/** 
* Basic sleep command without any fancy configuration. 
* 
* @author nmalik 
*/  
public class SleepCommand extends HystrixCommand<String> {  
    private final String msec;  
  
    public SleepCommand(String msec, String clientId) {  
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("sleep")));  
  
        this.msec = msec;  
    }  
  
    @Override  
    protected String run() throws Exception {  
        int i = Integer.valueOf(msec);  
        Thread.sleep(i);  
        return msec;  
    }  
  
    @Override  
    protected String getFallback() {  
        return msec + " TIMEOUT";  
    }  
}
```
 
This example focuses on Synchronous Execution but it's also possible to do Asynchronous Execution.  To execute synchronously simply create a new instance of the command and execute it.  This example executes with a 100 msec sleep and prints the output to stdout:
* `System.out.println(new SleepCommand("100").execute());`
 
## Servo: JVM
This example is publishing just JVM stats to Graphite.  A later section will include additions for collecting Hystrix metrics.
* Servo includes a JvmMetricPoller class to collect JVM stats.
* Servo includes a GraphiteMetricObserver to push collected metrics to a graphite instance.
 
Required maven dependencies:
```xml
<dependency>  
    <groupId>com.netflix.servo</groupId>  
    <artifactId>servo-core</artifactId>  
    <version>0.4.44</version>  
</dependency>  
<dependency>  
    <groupId>com.netflix.servo</groupId>  
    <artifactId>servo-graphite</artifactId>  
    <version>0.4.44</version>  
</dependency>  
```
 
A simple class that sets all these up in a static initialize method:
```java
import com.netflix.hystrix.strategy.HystrixPlugins;  
import com.netflix.servo.publish.BasicMetricFilter;  
import com.netflix.servo.publish.JvmMetricPoller;  
import com.netflix.servo.publish.MetricObserver;  
import com.netflix.servo.publish.PollRunnable;  
import com.netflix.servo.publish.PollScheduler;  
import com.netflix.servo.publish.graphite.GraphiteMetricObserver;  
import java.util.concurrent.TimeUnit;  
  
public class ServoSetupSimple {  
    public static final void initialize() {  
        String prefix = System.getenv("HOSTNAME");  
        String host = System.getenv("GRAPHITE_HOSTNAME");  
        String port = System.getenv("GRAPHITE_PORT");  
  
        String addr = host + ":" + port;  
        MetricObserver observer = new GraphiteMetricObserver(prefix, addr);  
  
        // start poll scheduler  
        PollScheduler.getInstance().start();  
  
        // create and register registery poller  
        PollRunnable registeryTask = new PollRunnable(new MonitorRegistryMetricPoller(), BasicMetricFilter.MATCH_ALL, observer);  
        PollScheduler.getInstance().addPoller(registeryTask, 5, TimeUnit.SECONDS);  
  
        // create and register jvm poller  
        PollRunnable jvmTask = new PollRunnable(new JvmMetricPoller(), BasicMetricFilter.MATCH_ALL, observer);  
        PollScheduler.getInstance().addPoller(jvmTask, 5, TimeUnit.SECONDS);  
    }  
}  
```

## Servo: Hystrix
An extension of the JVM only example above, this shows how to register all Hystrix metrics with servo and publish them.  Hystrix provides a plugin that makes is very simple to add this capability.  Note that to have this work you must add to the "JVM" example above.  If you do not want JVM stats published simply do not create and register the jvm poller.
 
Required maven dependencies:
```xml
<dependency>  
    <groupId>com.netflix.hystrix</groupId>  
    <artifactId>hystrix-servo-metrics-publisher</artifactId>  
    <version>1.1.2</version>  
</dependency>  
```
 
Code to register the Hystrix metrics with Servo:
```java
import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;  

...  
    HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());  
...
```
 
