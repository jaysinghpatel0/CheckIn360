# CheckIn360 — Attendance Management App

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-blue)
![Firebase](https://img.shields.io/badge/Backend-Firebase-orange)

A production-ready employee attendance management app with GPS verification,
real-time cloud sync, and leave management.

## Screenshots
<table>
  <tr>
    <td valign="top" width="16.6%"><img src="https://github.com/user-attachments/assets/ea03bbc8-5add-43a7-b7f1-cbc9c994a154"></td>
    <td valign="top" width="16.6%"><img src="https://github.com/user-attachments/assets/5a1552ac-601a-4746-8616-11ceadcf1c51"></td>
    <td valign="top" width="16.6%"><img src="https://github.com/user-attachments/assets/0d22ae6e-27c2-47e5-b03d-313a7add4788"></td>
    <td valign="top" width="16.6%"><img src="https://github.com/user-attachments/assets/26a54cc7-74d6-4dc0-a622-416631c9879a"></td>
    <td valign="top" width="16.6%"><img src="https://github.com/user-attachments/assets/4cf5b9ec-85f3-40e9-8aca-211e916fc347"></td>
    <td valign="top" width="16.6%"><img src="https://github.com/user-attachments/assets/b7916e21-4a69-4691-afec-5ae060f5f6c3"></td>
  </tr>
  <tr>
    <td align="center"><b>Login Screen</b></td>
    <td align="center"><b>Punch In</b></td>
    <td align="center"><b>Punch Out</b></td>
    <td align="center"><b>Attendance history</b></td>
    <td align="center"><b>Apply Leave</b></td>
    <td align="center"><b>Profile</b></td>
  </tr>
</table>

## Features
- [ ] Firebase Auth (email/password login)
- [ ] GPS-verified punch in/out
- [ ] Real-time Firestore data sync
- [ ] Monthly attendance history
- [ ] Leave application & tracking
- [ ] Employee profile management
- [ ] Admin dashboard

## Tech Stack
| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Architecture | MVVM + Repository |
| Backend | Firebase Auth + Firestore |
| Location | FusedLocationProvider |
| Local DB | Room Database |
| Dependency Injection | Hilt |
| UI | Jetpack Compose |
| Tools | Android Studio, Git |

## Architecture
MVVM pattern with Repository layer
UI → ViewModel → Repository → Firebase/Room

## Project Management
- Jira: Sprint-based Agile development
- Design: Figma
- Timeline: 7 weeks

## Setup Instructions
1. Clone the repo
2. Add google-services.json (Firebase console)
3. Build and run in Android Studio

## Developer
Jay Singh — Android Developer | MBA IT | B.Tech CSE (AKTU)   
LinkedIn: (https://www.linkedin.com/in/jaysinghpatel0/)
