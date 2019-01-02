import { Actions, ofActionErrored, ofActionSuccessful, Store } from '@ngxs/store';
import { Injectable } from '@angular/core';
import { Login, LoginSuccess } from '@bdroid/auth';
import { RouterNavigation } from '@ngxs/router-plugin';
import { RouterStateData } from '@bdroid/core';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { PageTitleService } from '../services/page-title.service';
import { GoogleAnalyticsService } from '../services/google-analytics.service';

@Injectable({
  providedIn: 'root',
})
export class EventBus {
  constructor(
    private actions$: Actions,
    private store: Store,
    private analytics: GoogleAnalyticsService,
    private pageTitle: PageTitleService,
  ) {
    this.actions$.pipe(ofActionSuccessful(Login)).subscribe(action => console.log('Login........Action Successful'));
    this.actions$.pipe(ofActionErrored(Login)).subscribe(action => console.log('Login........Action Errored'));
    this.actions$
      .pipe(ofActionSuccessful(LoginSuccess))
      .subscribe((action: LoginSuccess) => {
        this.analytics.setUsername(action.payload.preferred_username);
      });

    // FIXME : https://github.com/ngxs/store/issues/542
    this.actions$
      .pipe(
        ofActionSuccessful(RouterNavigation),
        map((action: RouterNavigation) => action.routerState as any),
        distinctUntilChanged((previous: RouterStateData, current: RouterStateData) => {
          return previous.url === current.url;
        }),
      )
      .subscribe(data => {
        console.log(data.breadcrumbs);
        this.pageTitle.setTitle(data.breadcrumbs);
        this.analytics.setPage(data.url);
      });
  }
}
