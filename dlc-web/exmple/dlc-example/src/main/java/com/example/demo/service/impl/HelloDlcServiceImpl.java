package com.example.demo.service.impl;

import com.example.demo.service.HelloDlcService;
import org.springframework.stereotype.Service;

/**
 * ClassName: HelloDlcServiceImpl <br/>
 * Description: HelloDlcServiceImpl <br/>
 * Date: 2017/7/17 13:57 <br/>
 * Company: TravelSky <br/>
 *
 * @author sxp(sxp travelsky.com)<br >
 * @version 1.0 <br/>
 */
@Service
public class HelloDlcServiceImpl implements HelloDlcService {

    /**
     * Hello dlc.
     *
     * @param name the name
     * @return the string
     */
    @Override
    public String helloDlc(String name) {
        return "Hello, " + name;
    }
}