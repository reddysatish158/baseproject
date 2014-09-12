package org.mifosplatform.organisation.officeadjustments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

	@Entity
	@Table(name = "m_adjustments")
	public class OfficeAdjustments extends AbstractAuditableCustom<AppUser, Long> {

		@Column(name = "office_id", nullable = false, length = 20)
		private Long office_id;

		@Column(name = "adjustment_date", nullable = false)
		private Date adjustment_date;

		@Column(name = "adjustment_code", nullable = false, length = 20)
		private int adjustment_code;

		@Column(name = "adjustment_type", nullable = false, length = 12)
		private String adjustment_type;

		@Column(name = "adjustment_amount", nullable = true, length = 22)
		private BigDecimal amount_paid;

		@Column(name = "is_deleted")
		private char isDeleted='N';

		@Column(name = "remarks", nullable = true, length = 200)
		private String Remarks;
		
		public OfficeAdjustments(){
			
		}
		
		public static OfficeAdjustments fromJson(final JsonCommand command) {
			    final LocalDate adjustmentDate = command.localDateValueOfParameterNamed("adjustment_date");
		        final Long adjustmentCode = command.longValueOfParameterNamed("adjustment_code");
		        final String adjustmentType = command.stringValueOfParameterNamed("adjustment_type");
		        final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amount_paid");
		        final String remarks = command.stringValueOfParameterNamed("Remarks");
				return new OfficeAdjustments(command.entityId(),adjustmentDate,adjustmentCode,adjustmentType,amountPaid,remarks);
		}
		
		public OfficeAdjustments(Long office_id, LocalDate adjustment_date,
				Long adjustment_code, String adjustment_type,
				BigDecimal amount_paid, String Remarks) {
			
			this.office_id = office_id;
			this.adjustment_date = adjustment_date.toDate();
			this.adjustment_code =(adjustment_code).intValue();
			this.adjustment_type = adjustment_type;
			this.amount_paid = amount_paid;
			this.Remarks = Remarks;
			
		}
		
		public static OfficeAdjustments fromJson(Long office_id, LocalDate adjustment_date,
				Long adjustment_code, String adjustment_type,
				BigDecimal amount_paid,String Remarks){
			return new OfficeAdjustments(office_id,adjustment_date, adjustment_code,
					adjustment_type, amount_paid, Remarks);
		}

		public Long getOffice_id() {
			return office_id;
		}

		public void setOffice_id(Long office_id) {
			this.office_id = office_id;
		}

		public Date getAdjustment_date() {
			return adjustment_date;
		}

		public void setAdjustment_date(Date adjustment_date) {
			this.adjustment_date = adjustment_date;
		}

		public int getAdjustment_code() {
			return adjustment_code;
		}

		public void setAdjustment_code(int adjustment_code) {
			this.adjustment_code = adjustment_code;
		}

		public String getAdjustment_type() {
			return adjustment_type;
		}

		public void setAdjustment_type(String adjustment_type) {
			this.adjustment_type = adjustment_type;
		}

		public BigDecimal getAmount_paid() {
			return amount_paid;
		}

		public void setAmount_paid(BigDecimal amount_paid) {
			this.amount_paid = amount_paid;
		}

		public String getRemarks() {
			return Remarks;
		}

		public void setRemarks(String remarks) {
			Remarks = remarks;
		}
}
