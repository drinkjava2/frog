/*
 * Copyright 2018 the original author or authors. 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog.organ.frog;

import com.gitee.drinkjava2.frog.Env;
import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.brain.Organ;
import com.gitee.drinkjava2.frog.objects.Material;

/**
 * Move frog 1 unit if outputs of nerve cells active in this zone
 */
public class FrogMoves {
	public static class MoveUp extends Organ {
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			if (beActivedByCells(a)) {
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				a.y--;
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				Env.setMaterial(a.x, a.y, a.no);
			}
		}
	}

	public static class MoveDown extends Organ {
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			if (beActivedByCells(a)) {
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				a.y++;
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				Env.setMaterial(a.x, a.y, a.no);
			}
		}
	}

	public static class MoveLeft extends Organ {
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			if (beActivedByCells(a)) {
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				a.x--;
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				Env.setMaterial(a.x, a.y, a.no);
			}
		}
	}

	public static class MoveRight extends Organ {
		private static final long serialVersionUID = 1L;

		@Override
		public void active(Animal a) {
			if (beActivedByCells(a)) {
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				a.x++;
				Env.clearMaterial(a.x, a.y, Material.FROG_TAG);
				Env.setMaterial(a.x, a.y, a.no);
			}
		}

	}

}
