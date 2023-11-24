package com.yandex.travelmap.model

import javax.persistence.*

@Entity
@Table(name = "countries")
class Country(
    @Id
    @Column(name = "iso")
    val iso: String,

    @Column(name = "name", unique = true)
    val name: String,

    @ManyToMany(mappedBy = "visitedCountries", cascade = [CascadeType.ALL])
    val visitors: MutableSet<AppUser>,

    @ManyToMany(mappedBy = "desiredCountries", cascade = [CascadeType.ALL])
    val desirers: MutableSet<AppUser>
)
