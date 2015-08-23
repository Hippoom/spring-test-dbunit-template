package com.github.hippoom.springtestdbunittemplate.sample;

import java.util.List;

public interface GalleryQuery {

    List<Gallery> byEventStatus(Event.Status status);

}
