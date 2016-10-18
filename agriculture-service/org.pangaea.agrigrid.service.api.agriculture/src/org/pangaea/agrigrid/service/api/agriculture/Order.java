package org.pangaea.agrigrid.service.api.agriculture;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Order
implements Serializable{
	/**
	 * Default constructor.
	 */
	public Order(){}

	/**
	 * Constructor.
	 * @param fieldName Field to be sorted
	 * @param direction Sort direction ("ASCENDANT" or "DESCENDANT")
	 */
	public Order(String fieldName, String direction) {
		this.fieldName = fieldName;
		this.direction = direction;
	}

	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}

	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * Returns a field name.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets a field name.
	 */
	public void setFieldName(String field) {
		this.fieldName = field;
	}

	/**
	 * Returns a direction.
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * Sets a direction.
	 * "ASCENDANT"(ascending order) and also "DESCENDANT"(descending order)
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	private String fieldName;
	private String direction;
	private static final long serialVersionUID = 2606007470664315633L;
}
