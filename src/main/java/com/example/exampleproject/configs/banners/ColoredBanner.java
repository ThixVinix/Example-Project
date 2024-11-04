package com.example.exampleproject.configs.banners;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The ColoredBanner class implements the Banner interface to display a customized and colorized startup banner for
 * a Spring Boot application (ASCII Art).
 */
public class ColoredBanner implements Banner {


    /**
     * Displays a customized and colorized startup banner when the Spring Boot application starts.
     *
     * @param environment the environment containing the application's current configuration.
     * @param sourceClass the source class for the Spring Boot application.
     * @param out         the print stream to which the banner will be printed.
     */
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

        String[] activeProfilesArray = environment.getActiveProfiles();
        String activeProfiles = (activeProfilesArray.length > 0) ?
                String.join(", ", activeProfilesArray) : "No active profiles";
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String banner = String.format("""
                 _____                                 _         ______                 _              _
                |  ___|                               | |        | ___ \\               (_)            | |
                | |__  __  __  __ _  _ __ ___   _ __  | |  ___   | |_/ / _ __   ___     _   ___   ___ | |_
                |  __| \\ \\/ / / _` || '_ ` _ \\ | '_ \\ | | / _ \\  |  __/ | '__| / _ \\   | | / _ \\ / __|| __|
                | |___  >  < | (_| || | | | | || |_) || ||  __/  | |    | |   | (_) |  | ||  __/| (__ | |_
                \\____/ /_/\\_\\ \\__,_||_| |_| |_|| .__/ |_| \\___|  \\_|    |_|    \\___/   | | \\___| \\___| \\__|
                                               | |                                    _/ |
                                               |_|                                   |__/
                Powered by Spring Boot %s
                
                Active Profiles: %s
                Start Time: %s
                """, SpringBootVersion.getVersion(), activeProfiles, formattedDateTime);

        out.println(AnsiOutput.toString(AnsiStyle.BOLD, AnsiColor.BLUE, banner));
    }
}