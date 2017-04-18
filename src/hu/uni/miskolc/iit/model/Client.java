package hu.uni.miskolc.iit.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "clientId" })
public class Client {

	private Long clientId;
	
	@NotNull
	@Size(min = 2, max = 50, message = "A n�v hossza minimum 2, maximum 50 karakter kell legyen.")
	private String name;
	
	@NotNull
	//@Pattern(regexp = "\\d{4} [a-zA-Z]+, [a-zA-Z]+ \\d+")
	private String address;
	
	@NotNull
	@Pattern(regexp = "\\(\\d{2}\\)-\\d{3}-\\d{4}", message = "A megadott telefonsz�mnak a k�vetkez� mint�nak kell megfelelnie: (01)-234-5678")
	private String phoneNumber;
	
	@NotNull
	@Pattern(regexp = "\\d{6}[a-zA-Z]{2}", message = "A szem�lyi igazolv�nysz�mnak a k�vetkez� mint�nak kell megfelelnie: 123456AB")
	private String personalId;
	
	private Status status = Status.ACTIVE;
	
	private List<Account> accounts = new ArrayList<>();

	public enum Status {
		
		ACTIVE, DELETED;
		
	}

	@Override
	public String toString() {
		return String.format("�gyf�lazonos�t�: %d (N�v: %s, szem�lyi igazolv�nysz�m: %s)", clientId, name, personalId);
	}
	
}
