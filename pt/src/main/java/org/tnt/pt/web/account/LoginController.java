package org.tnt.pt.web.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tnt.pt.dmsentity.User;
import org.tnt.pt.service.account.AccountService;
import org.tnt.pt.util.PTPARAMETERS;
import org.tnt.pt.vo.BusinessVO;
import org.tnt.pt.vo.LoginVO;

/**
 * LoginController负责打开登录页面(GET请求)和登录出错页面(POST请求)，

 * 真正登录的POST请求由Filter完成,
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController {

	@Autowired
	private AccountService accountService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String login() {
		return "index/login";
	}
	
	@RequestMapping(value="/loginin",method = RequestMethod.POST)
	public String loginin(Model model,@ModelAttribute LoginVO loginVO,HttpServletRequest request ) {
		User user = new User();
		user = accountService.findUserByLoginName(loginVO);
		/*if(loginVO.getUserName().equals("test1")){
			user.setRole_name(PTPARAMETERS.ROLE_NAME[0]);
			user.setUserName("test1");
		}else if(loginVO.getUserName().equals("test2")){
			user.setRole_name(PTPARAMETERS.ROLE_NAME[1]);
			user.setUserName("test2");
		}else if(loginVO.getUserName().equals("test3")){
			user.setRole_name(PTPARAMETERS.ROLE_NAME[2]);
			user.setUserName("test3");
		}else if(loginVO.getUserName().equals("test4")){
			user.setRole_name(PTPARAMETERS.ROLE_NAME[3]);
			user.setUserName("test4");
		}else if(loginVO.getUserName().equals("test5")){
			user.setRole_name(PTPARAMETERS.ROLE_NAME[4]);
			user.setUserName("test5");
		}*/
		if(user!=null){
			request.getSession().setAttribute("user", user);
			return "index/default";
		}else{
			request.setAttribute("error", "登陆失败 请重试");
			model.addAttribute("model", loginVO);
			return "index/login";
		}
		
	}
	
	@RequestMapping(value="logout",method = RequestMethod.GET)
	public String logout(Model model,@ModelAttribute LoginVO loginVO,HttpServletRequest request ) {
		User user = (User) request.getSession().getAttribute("user");
		if(user!=null){
			loginVO.setUserName(user.getUserName());
			model.addAttribute("model", loginVO);
			return "index/login";
		}else{
			return "index/login";
		}
	}
	
	@RequestMapping(value="roleChange/{userName}",method = RequestMethod.GET)
	public String roleChange(Model model,@PathVariable("userName") String userName) {
		List<String> roleList = new ArrayList<String>();
		roleList = accountService.getRolesByName(userName);
		roleList.add(PTPARAMETERS.ROLE_NAME[0]);
		model.addAttribute("roleList", roleList);
		return "index/roleList";
	}
	
	@RequestMapping(value="changeConfirm",method = RequestMethod.GET)
	public String roleChange(Model model,HttpServletRequest request ) {
		User user = (User)request.getSession().getAttribute("user");
		String roleName = request.getParameter("roleName");
		Map<String,String> map = new HashMap<String,String>();
		map.put("userName", user.getUserName());map.put("roleName", roleName);
		User newUser = accountService.findByRoleName(map);
		request.getSession().removeAttribute("user");
		request.getSession().setAttribute("user", newUser);
		return "index/default";
	}
	
	@RequestMapping(value="/{path}",method = RequestMethod.GET)
	public String redirect(@PathVariable("path") String path) {
		if("header".equals(path)){
			return "index/header";
		}else if("footer".equals(path)){
			return "index/footer";
		}else if("left".equals(path)){
			return "index/left";
		}else if("breadcrumbs".equals(path)){
			return "index/breadcrumbs";
		}else{
			return "index/main";
		}
		
	}

	@RequestMapping(method = RequestMethod.POST)
	public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName, Model model) {
		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
		return "account/login";
	}

}
