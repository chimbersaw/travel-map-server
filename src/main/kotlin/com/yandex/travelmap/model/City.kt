package com.yandex.travelmap.model

import javax.persistence.*

@Entity
@Table(name = "cities")
class City(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column
    val name: String,

    @ManyToOne
    @JoinColumn(name = "country_code", referencedColumnName = "iso")
    val country: Country,

    @ManyToMany(mappedBy = "visitedCities", cascade = [CascadeType.ALL])
    val visitors: MutableSet<AppUser>
)
