package com.googlecode.mjorm;

import java.math.BigDecimal;

public class City {

	private String id;
	private String name;
	private BigDecimal lat;
	private BigDecimal lon;
	private String[] zipCodes;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the lat
	 */
	public BigDecimal getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	/**
	 * @return the lon
	 */
	public BigDecimal getLon() {
		return lon;
	}
	/**
	 * @param lon the lon to set
	 */
	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}
	/**
	 * @return the zipCodes
	 */
	public String[] getZipCodes() {
		return zipCodes;
	}
	/**
	 * @param zipCodes the zipCodes to set
	 */
	public void setZipCodes(String[] zipCodes) {
		this.zipCodes = zipCodes;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		City other = (City) obj;
		if (lat == null) {
			if (other.lat != null) return false;
		} else if (!lat.equals(other.lat)) return false;
		if (lon == null) {
			if (other.lon != null) return false;
		} else if (!lon.equals(other.lon)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
	}

}
