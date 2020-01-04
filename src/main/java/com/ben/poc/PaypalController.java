package com.ben.poc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
public class PaypalController {
	
	@Autowired
	PaypalService paypalService;
	
	public static final String SUCESS_URL="pay/sucess";
	public static final String CANCEL_URL="pay/cancel";
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@PostMapping("/pay")
	public String payment(@ModelAttribute("orderDto") OrderDTO orderDto) {
		try {
			Payment payment = paypalService.createPayment(orderDto.getPrice(), orderDto.getCurrency(),
					orderDto.getMethod(), orderDto.getIntent(), orderDto.getDescription(), "http://localhost:9090/"+CANCEL_URL,
					"http://localhost:9090/"+SUCESS_URL);
			for(Links link: payment.getLinks()) {
				if(link.getRel().contains("approval_url")) {
					return "redirect:"+link.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		
		return "redirect:/";
		
	}
	
	@GetMapping(value = CANCEL_URL)
	public String cancelPay() {
		return "cancel";
	}
	
	@GetMapping(value=SUCESS_URL)
	public String successPay( @RequestParam("paymentId") String paymentId , @RequestParam("payerId") String payerId) {
		try {
			Payment payment = paypalService.excutePayment(paymentId, payerId);
			System.out.println(payment.toJSON());
			if(payment.getState().equals("approved")) {
				return "sucess";
			}
		} catch (PayPalRESTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/";
		
	}

}
