
# Setting up a conda environment with the necessary libraries
conda create -n mgrl3-ml-proj4 python=3
conda activate mgrl3-ml-proj4
conda install numpy matplotlib ipykernel jupyter
pip install gym
pip install pymdptoolbox

# Library References

## gym
- [top](https://gym.openai.com/docs/)

## pymdptoolbox
- [top](https://pymdptoolbox.readthedocs.io/en/latest/index.html)
- [MDP solvers](https://pymdptoolbox.readthedocs.io/en/latest/api/mdp.html)
