package com.example.musicplayer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	String username;
	
	@Column
	String password;
	
	@Column
	String role;

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", permission=" + role + "]";
	}
}
