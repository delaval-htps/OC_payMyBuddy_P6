package com.paymybuddy.controllers;

import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class CustomErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ServletWebRequest webRequest = new ServletWebRequest(request);

        Map<String, Object> eAttributes = this.errorAttributes.getErrorAttributes(webRequest,
                ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE));

        model.addAttribute("errorAttributes", eAttributes);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                log.error(("errormessage: {} & errormessageURI: {}"),
                        request.getAttribute(RequestDispatcher.ERROR_MESSAGE),
                        request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

                return "error-404";

            }
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                log.error(("errormessage: {} & errormessageURI: {}"),
                        request.getAttribute(RequestDispatcher.ERROR_MESSAGE),
                        request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
                return "error-505";

            }
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                log.error(("errormessage: {} & errormessageURI: {}"),
                        request.getAttribute(RequestDispatcher.ERROR_MESSAGE),
                        request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
                return "error-403";

            }

        }
        return "error";
    }


}
