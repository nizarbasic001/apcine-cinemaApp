package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "original_title") val originalTitle: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "poster_url") val posterUrl: String? = null,
    @Json(name = "backdrop_url") val backdropUrl: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "year") val year: Int? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "genres") val genres: List<String>? = emptyList(),
    @Json(name = "cast_members") val castMembers: List<String>? = emptyList(),
    @Json(name = "director") val director: String? = null,
    @Json(name = "imdb_id") val imdbId: String? = null,
    @Json(name = "tmdb_id") val tmdbId: Long? = null,
    @Json(name = "magnet_link") val magnetLink: String? = null,
    @Json(name = "language") val language: String? = null,
    @Json(name = "featured") val featured: Boolean? = false,
    @Json(name = "published") val published: Boolean? = true,
    @Json(name = "stream_url") val streamUrl: String? = null,
    @Json(name = "subtitle_url") val subtitleUrl: String? = null,
    @Json(name = "title_ar") val titleAr: String? = null,
    @Json(name = "description_ar") val descriptionAr: String? = null,
    @Json(name = "genres_ar") val genresAr: List<String>? = emptyList(),
    @Json(name = "director_ar") val directorAr: String? = null,
    @Json(name = "cast_ar") val castAr: List<String>? = emptyList()
) {
    val displayRating: String
        get() = if (rating != null && rating > 0) String.format("%.1f", rating) else "NR"

    fun getDisplayRuntime(isArabic: Boolean = false): String {
        if (runtime == null || runtime <= 0) return if (isArabic) "غير محدد" else "N/A"
        val h = runtime / 60
        val m = runtime % 60
        return if (isArabic) "$h س $m د" else "${h}h ${m}m"
    }

    val displayRuntime: String
        get() = getDisplayRuntime(false)

    fun getLocalizedTitle(isArabic: Boolean): String {
        return if (isArabic && !titleAr.isNullOrBlank()) titleAr else title
    }

    fun getLocalizedDescription(isArabic: Boolean): String? {
        return if (isArabic && !descriptionAr.isNullOrBlank()) descriptionAr else description
    }

    fun getLocalizedGenres(isArabic: Boolean): List<String> {
        if (isArabic) {
            if (!genresAr.isNullOrEmpty()) return genresAr
            return genres?.map { translateGenre(it, true) } ?: emptyList()
        }
        return genres ?: emptyList()
    }

    fun getLocalizedPrimaryGenre(isArabic: Boolean): String {
        val list = getLocalizedGenres(isArabic)
        return list.firstOrNull() ?: if (isArabic) "فيلم" else "Movie"
    }

    val primaryGenre: String
        get() = getLocalizedPrimaryGenre(false)

    fun getLocalizedGenresLabel(isArabic: Boolean): String {
        return getLocalizedGenres(isArabic).joinToString(" • ")
    }

    val genresLabel: String
        get() = getLocalizedGenresLabel(false)

    fun getLocalizedDirector(isArabic: Boolean): String? {
        return if (isArabic && !directorAr.isNullOrBlank()) directorAr else director
    }

    fun getLocalizedCastLabel(isArabic: Boolean): String {
        val list = if (isArabic && !castAr.isNullOrEmpty()) castAr else castMembers
        return list?.take(4)?.joinToString(", ") ?: ""
    }

    val castLabel: String
        get() = getLocalizedCastLabel(false)

    fun resolvePlayerUrl(websiteBaseUrl: String = "https://cinestream-movie-str-4phw.bolt.host/watch"): String {
        return "$websiteBaseUrl/$id"
    }

    fun resolveDirectStreamUrl(): String? {
        if (!streamUrl.isNullOrBlank()) return streamUrl.trim()
        if (!imdbId.isNullOrBlank()) return "https://vidsrc.sh/embed/movie/$imdbId"
        return null
    }

    companion object {
        fun translateGenre(genre: String, isArabic: Boolean): String {
            if (!isArabic) return genre
            return when (genre.trim().lowercase()) {
                "all" -> "الكل"
                "action" -> "حركة"
                "fantasy" -> "فانتازيا"
                "drama" -> "دراما"
                "comedy" -> "كوميديا"
                "sci-fi", "science fiction" -> "خيال علمي"
                "animation" -> "رسوم متحركة"
                "horror" -> "رعب"
                "thriller" -> "إثارة"
                "adventure" -> "مغامرة"
                "crime" -> "جريمة"
                "romance" -> "رومانسية"
                "mystery" -> "غموض"
                "documentary" -> "وثائقي"
                "tv", "series" -> "مسلسلات"
                else -> genre
            }
        }
    }
}
