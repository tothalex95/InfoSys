package hu.uni.miskolc.iit.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = { "client" })
@EqualsAndHashCode(of = { "accountId" })
public class Account {

	private Long accountId;
	
	@NotNull
	private Client client;
	
	private Long sequenceNumber;
	
	@NotNull
	@Min(value = 0, message = "Az egyenleg nem lehet negatív.")
	private Float balance;
	
	private Boolean closed = false;
	
}
