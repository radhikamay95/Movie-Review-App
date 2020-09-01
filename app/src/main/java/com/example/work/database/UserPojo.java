package com.example.work.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserPojo {
    @Id(autoincrement = true)
    private Long userid;
    @Unique
    private String emailid;

    private String name;
    private String password;
    private String confirmPassword;
    private String gender;
    private int age;

    public UserPojo() {
        super();
    }

    @Keep
    public UserPojo(Long userid, String emailid, String name, String password, String confirmPassword, String gender, int age) {
        this.userid = userid;
        this.emailid = emailid;
        this.name = name;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.gender = gender;
        this.age = age;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
