# Product Requirements Document (PRD): Mettasphere

## 1. Executive Summary
**Project Name:** Mettasphere  
**Tagline:** Don’t focus. Just Smile, Relax, and Observe.  
**Core Objective:** To provide a digital companion for 6R meditation that prioritizes the cessation of tension ($Taṇhā$) and the cultivation of "Tranquil Awareness" as described in the Anupada Sutta (MN 111).

---

## 2. Target Audience
* **Newcomers:** Individuals seeking a joyful, relaxation-based alternative to "dry" concentration meditation.
* **TWIM Practitioners:** Students of the Bhante Vimalaramsi tradition seeking a structured way to track "Anupada" (step-by-step) progress.

---

## 3. Functional Requirements

### 3.1 Post-Session Questionnaire (The Core)
The application must capture qualitative data immediately following a session to objectify the "movement" of the mind.

#### **A. Recognition Speed**
* **Delayed:** Lost in thought for minutes before noticing.
* **Mid-thought:** Caught the thought while it was active.
* **Immediate:** Caught the thought as it started.
* **Pre-thought:** Noticed the "tightness" in the head before the thought formed.

#### **B. Release & Relax Quality**
* **Release Check:** User identifies if they used Suppression (pushing), Analytical (thinking about it), or 6R Release (letting be).
* **Relaxation Depth:** * *Surface:* Relaxing face/shoulders.
    * *Internal:* Physically dissolving the "clench" inside the brain-sac.

#### **C. Re-smile Resonance**
* A metric to determine if the smile successfully "gladdened" the mind or remained a mechanical gesture.

### 3.2 Hindrance Tracking (*Nīvarana*)
A multi-select checklist to identify impersonal mental states:
* **Sensual Desire** (*Kāmacchanda*)
* **Ill-Will / Aversion** (*Vyāpāda*)
* **Sloth and Torpor** (*Thīna-middha*)
* **Restlessness and Regret** (*Uddhacca-kukkucca*)
* **Skeptical Doubt** (*Vicikicchā*)

### 3.3 Jhana Factor "Weather Report"
Mapping post-sit feelings to Jhana factors without technical labels:
* Bubbly energy/tingling $\rightarrow$ *Pīti* (Zest)
* Deep, cool comfort $\rightarrow$ *Sukha* (Happiness)
* Still, neutral balance $\rightarrow$ *Upekkha* (Equanimity)

---

## 4. UX & Design Principles
* **Theme:** "Inner Sunshine." Use high whitespace, soft gradients, and rounded UI elements.
* **Tone:** Non-judgmental and process-oriented. Replace "Goal" with "Observation."
* **The "Gap":** UI should encourage resting in the stillness found between the 6R cycle and the return to the object.

---

## 5. Analytics & Progress Reporting
* **Sati Sensitivity Trend:** A line graph showing the transition from "Delayed" to "Pre-thought" recognition.
* **The Tanha-Release Ratio:** Correlation between specific Hindrances and the success rate of "Internal" relaxation.
* **Hindrance Heatmap:** Identifying which *Nīvarana* is most frequent to provide targeted 6R tips.

---

## 6. Technical Specifications
* **License:** Apache 2.0.
* **Development Philosophy:** AI-assisted development guided by `agent.md`.
* **Data Privacy:** Local-first storage for sensitive meditation logs and mental states.

---

## 7. Roadmap
* **v1.0:** Core 6R timer and granular post-session questionnaire.
* **v2.0:** Daily life "Smile & Relax" prompts and MN 111 Sutta-insight notifications.