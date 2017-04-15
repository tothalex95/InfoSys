package hu.uni.miskolc.iit.view.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import hu.uni.miskolc.iit.model.Account;
import hu.uni.miskolc.iit.service.AccountService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ManagedBean(name = "accountConverter")
public class AccountConverter implements Converter {

	@ManagedProperty("#{accountService}")
	private AccountService service;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return service.getAccountByAccountId(Long.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Account account = (Account) value;
		return account.getAccountId().toString();
	}

}
