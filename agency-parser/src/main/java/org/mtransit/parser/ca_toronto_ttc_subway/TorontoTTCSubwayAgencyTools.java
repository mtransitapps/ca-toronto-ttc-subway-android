package org.mtransit.parser.ca_toronto_ttc_subway;

import static org.mtransit.commons.CleanUtils.SPACE;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.TorontoTTCCommons;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// https://open.toronto.ca/dataset/ttc-routes-and-schedules/
// OLD: http://opendata.toronto.ca/TTC/routes/OpenData_TTC_Schedules.zip
// http://opendata.toronto.ca/toronto.transit.commission/ttc-routes-and-schedules/OpenData_TTC_Schedules.zip
public class TorontoTTCSubwayAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new TorontoTTCSubwayAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	public String getAgencyName() {
		return "TTC";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_SUBWAY;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	private static final Pattern EXTRACT_RLN = Pattern.compile("(line [0-9] \\(([^)]*)\\))", Pattern.CASE_INSENSITIVE);
	private static final String EXTRACT_RLN_REPLACEMENT = "$2";

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, routeLongName);
		routeLongName = EXTRACT_RLN.matcher(routeLongName).replaceAll(EXTRACT_RLN_REPLACEMENT);
		return CleanUtils.cleanLabel(routeLongName);
	}

	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	@Nullable
	public String fixColor(@Nullable String color) {
		final String fixedColor = TorontoTTCCommons.fixColor(color, true);
		if (fixedColor != null) {
			return fixedColor;
		}
		return super.fixColor(color);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern ENDS_WITH_STATION_ = Pattern.compile("( (station)$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, tripHeadsign);
		tripHeadsign = ENDS_WITH_STATION_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.fixMcXCase(tripHeadsign);
		tripHeadsign = CleanUtils.CLEAN_AT.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STATION_ = CleanUtils.cleanWord("station");

	private static final Pattern ENDS_WITH_PLATFORM_ = Pattern.compile("( (platform)$)", Pattern.CASE_INSENSITIVE);

	private static final Pattern SUBWAY_ = CleanUtils.cleanWord("subway");

	private static final Pattern DASH_ = Pattern.compile("( - )", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_TOWARDS_ = Pattern.compile("( towards .*$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = DASH_.matcher(gStopName).replaceAll(SPACE);
		gStopName = ENDS_WITH_TOWARDS_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = ENDS_WITH_PLATFORM_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = SUBWAY_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = STATION_.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, gStopName);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.fixMcXCase(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
