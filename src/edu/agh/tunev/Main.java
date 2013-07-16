/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package edu.agh.tunev;

import edu.agh.tunev.ui.MainFrame;

public class Main {

	/**
	 * Tutaj rejestrujemy wszystkie nasze modele, które mają być widoczne w UI.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame.register(edu.agh.tunev.model.cellular.Model.class);
		MainFrame.register(edu.agh.tunev.model.jrakoczy.Model.class);

		new MainFrame();
	}
}
