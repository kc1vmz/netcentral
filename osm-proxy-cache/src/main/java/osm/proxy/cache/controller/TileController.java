package osm.proxy.cache.controller;

/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import osm.proxy.cache.accessor.TileAccessor;
import osm.proxy.cache.objects.Mode;
import osm.proxy.cache.objects.Statistics;

@Controller("/api/v1/tiles") 
@Secured(SecurityRule.IS_ANONYMOUS) 
public class TileController {

    private
    @Inject TileAccessor tileAccessor;

    @Get("/{z}/{x}/{y}")
    @Produces(MediaType.IMAGE_PNG)
    public byte [] getOne(HttpRequest<?> request, @PathVariable String z, @PathVariable String x, @PathVariable String y) {
        byte [] fileContent = tileAccessor.fetch(x,y,z);
        return fileContent;
    }

    @Delete
    public void deleteAll(HttpRequest<?> request) {
        tileAccessor.deleteAll();
        return;
    }

    @Put("/modes/{mode}")
    public Mode updateMode(HttpRequest<?> request, @PathVariable String mode) {
        Mode ret = new Mode();
        ret.setMode(tileAccessor.updateMode(mode));
        return ret;
    }

    @Get("/modes")
    public Mode getMode(HttpRequest<?> request) {
        Mode ret = new Mode();
        ret.setMode(tileAccessor.getMode());
        return ret;
    }

    @Get("/statistics")
    public Statistics getStatistics(HttpRequest<?> request) {
        return tileAccessor.getStatistics();
    }

    @Get("/precaches/degrees/{lat}/{lon}") 
    public String preCacheByLatLonDegrees(HttpRequest<?> request, @PathVariable Float lat, @PathVariable Float lon) {
        tileAccessor.cacheByLatLonDegrees(lat, lon);
        return "ok";
    }

    @Get("/precaches/aprs/{lat}/{lon}") 
    public String preCacheByLatLonAPRS(HttpRequest<?> request, @PathVariable String lat, @PathVariable String lon) {
        tileAccessor.cacheByLatLonAPRS(lat, lon);
        return "ok";
    }
}
