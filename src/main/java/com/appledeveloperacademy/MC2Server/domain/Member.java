package com.appledeveloperacademy.MC2Server.domain;

import com.appledeveloperacademy.MC2Server.domain.superclass.CreationModificationLog;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member extends CreationModificationLog {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    private String username;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberRoom> memberRooms = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<CatType> catTypes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<HealthTag> healthTags = new ArrayList<>();

    @Column(unique = true)
    private String usercode;

}