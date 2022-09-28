package no.uio.adasam.adasam_oblig2

data class AlpacaParty(
    val color: String,
    val id: String,
    val img: String,
    val leader: String,
    val name: String,
    var votes: Int,
    var prosent: Int,
    )

data class Party(
    val parties: List<AlpacaParty>
)

data class Vote(
    val id: String
)

data class DistrictThree(
    val id: Int?,
    val antStemmer: Int?
)










