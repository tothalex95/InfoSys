package hu.uni.miskolc.iit.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "accountId" })
public class Account {

	private Long accountId;
	
	@NotNull
	private Client client;
	
	private Long sequenceNumber;
	
	@NotNull
	@Min(value = 0, message = "Az egyenleg nem lehet negat�v.")
	private Float balance;
	
	private Boolean closed = false;

	@Override
	public String toString() {
		return String.format("Sz�mlasz�m: %s (Sz�mlatulajdonos: %s)", (client.getClientId() + "-" + sequenceNumber), client.toString());
	}
	
}
