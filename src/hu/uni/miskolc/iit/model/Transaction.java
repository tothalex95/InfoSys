package hu.uni.miskolc.iit.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Transaction {

	public static final DateFormat DF = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, new Locale("hu", "HU"));
	
	private Long transactionId;
	
	@NotNull
	private Account account;
	
	@NotNull
	private Float amount;
	
	private String comment;
	
	@NotNull
	private Date date;
	
	private Account addressee;
	
}
