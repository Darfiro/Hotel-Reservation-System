import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import jwt_decode from 'jwt-decode';
import { ApiService } from 'src/app/_services';

import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  constructor(private http: HttpClient, private apiService: ApiService) { }

  login(username: string, password: string) {
    let authData = window.btoa(username + ':' + password);
    return this.http.post<HttpResponse<any>>(environment.apiBaseUrl + '/auth', null,
      {
        observe: 'response',
        headers: new HttpHeaders().append('Authorization', `Basic ${authData}`)
      });
  }

  setSession(token: string) {
    let decoded_token: any = jwt_decode(token);
    localStorage.setItem('role', decoded_token.role);
    localStorage.setItem('user', token);
  }

  private removeSession() {
    localStorage.removeItem('role');
    localStorage.removeItem('user');
  }

  isLogedIn() {
    if (localStorage.getItem('user') != null) {
      return true;
    }

    return false;
  }

  isAdmin() {
    return localStorage.getItem('role') == 'ADMIN';
  }

  logout() {
    this.apiService.discount = 0;
    this.removeSession();
  }

  getToken() {
    return localStorage.getItem('user');
  }
}