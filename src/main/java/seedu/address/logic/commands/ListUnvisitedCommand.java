package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSTAL;
import static seedu.address.model.Model.PREDICATE_SHOW_UNVISITED_RESTAURANTS;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.PostalData;
import seedu.address.model.restaurant.Postal;
import seedu.address.model.restaurant.Restaurant;

/**
 * Lists all restaurants that have yet to be visited in the food diary to the user.
 */
public class ListUnvisitedCommand extends Command {

    public static final String COMMAND_WORD = "listUnvisited";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": List unvisited "
            + "Parameters: "
            + PREFIX_POSTAL + "POSTAL CODE ";

    public static final String MESSAGE_SUCCESS = "Listed all unvisited restaurants";

    private int postal;

    public ListUnvisitedCommand(Postal postal) {
        this.postal = Integer.parseInt(postal.value);
    }
    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        /**
         * Implements Compartor for sorting @Restaurant based on distance from a @Postal
         *
         */
        class SortDistance implements Comparator<Restaurant> {
            private HashMap<Integer, Double> distanceData;
            private PostalData current;
            private double x;
            private double y;

            /**
             * Constructor: Init variables
             */
            SortDistance() {
                distanceData = new HashMap<>();
                current = model.getPostalData(postal).get();
                x = current.getX();
                y = current.getY();
            }

            /**
             *
             * @param a {@code Restaurant} of the first restaurant
             * @param b {@code Restaurant} of the second restaurant
             * @return 1 if a is nearer to current than b 0 if equal or -1 otherwise
             */

            public int compare(Restaurant a, Restaurant b) {
                int postalA = (Integer.parseInt(a.getPostal().value));
                int postalB = (Integer.parseInt(b.getPostal().value));

                if (!distanceData.containsKey(postalA)) {
                    Optional<PostalData> postalDataA = model.getPostalData(postalA);
                    if (postalDataA.isPresent()) {
                        double aX = postalDataA.get().getX();
                        double aY = postalDataA.get().getY();
                        double distance = (aX - x) * (aX - x) + (aY - y) * (aY - y);
                        distanceData.put(postalA, distance);
                    } else {

                    }
                }
                if (!distanceData.containsKey(postalB)) {
                    Optional<PostalData> postalDataB = model.getPostalData(postalB);
                    if (postalDataB.isPresent()) {
                        double bX = postalDataB.get().getX();
                        double bY = postalDataB.get().getY();
                        double distance = (bX - x) * (bX - x) + (bY - y) * (bY - y);
                        distanceData.put(postalB, distance);
                    } else {
                        distanceData.put(postalA, Double.MAX_VALUE);
                    }

                }


                double distA = distanceData.get(postalA);
                double distB = distanceData.get(postalB);


                if (distA - distB > 0) {
                    return 1;
                } else if (distA - distB < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
        requireNonNull(model);
        if (model.getPostalData(postal).isPresent()) {
            model.updateFilteredRestaurantListAndSort(PREDICATE_SHOW_UNVISITED_RESTAURANTS, new SortDistance());
        } else {
            model.updateFilteredRestaurantList(PREDICATE_SHOW_UNVISITED_RESTAURANTS);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ListUnvisitedCommand // instanceof handles nulls
                && postal == ((ListUnvisitedCommand) other).postal);
    }


}
