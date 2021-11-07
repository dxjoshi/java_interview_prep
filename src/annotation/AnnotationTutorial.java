package annotation;

import javax.jws.WebService;


@WebService(portName = "8080", serviceName = "dummy-ws")     // serviceName, portName are elements
@SuppressWarnings(value = "unchecked") // @SuppressWarnings("unchecked")
public class AnnotationTutorial implements Runnable {
    @Override       // without parentheses
    public void run() { }
}


